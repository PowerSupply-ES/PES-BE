package com.powersupply.PES.configuration;

import com.powersupply.PES.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final String secretKey;

    // 인증이 필요없는 경로 URL 목록
    private final List<String> skipUrls = Arrays.asList("/api/signin", "/api/signup", "/api/finduser");

    // 인증이 필요 없는 GET 요청의 URL 목록
    private final List<String> skipGetUrls = Arrays.asList("/api/problemlist/" , "/api/answer/**", "/api/comment/**", "/api/answerlist/**", "/api/rank");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 특정 GET 요청을 확인하고 건너뛰기
        AntPathMatcher pathMatcher = new AntPathMatcher();
        if ("GET".equals(method) && skipGetUrls.stream().anyMatch(uri -> pathMatcher.match(uri, requestURI))) {
            log.info("인증 필요 없음 : {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 인증이 필요없는 경로는 바로 통과
        if (skipUrls.contains(requestURI)) {
            log.info("인증 필요 없음 : {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 Authorization 키를 가진 쿠키 찾기
        String token = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.info("token : {}", token);
                    break;
                }
            }
        }

        // 토큰이 없거나 토큰이 시작이 "Bearer "가 아니라면 다음 필터로
        if (token == null) {
            log.error("Authorization이 잘못되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 실제 토큰 추출
        String actualToken = token;

        // 토큰이 만료되었는지 확인
        if (JwtUtil.isExpired(actualToken, secretKey)) {
            log.error("토큰이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // 멤버 학번 추출
        String memberId = JwtUtil.getMemberId(actualToken, secretKey);

        // 상태에 따른 권한 부여
        String memberStatus = JwtUtil.getMemberStatus(actualToken, secretKey);
        log.info("멤버 상태 : {}", memberStatus);
        SimpleGrantedAuthority authority;

        switch (memberStatus) {
            case "신입생":
                authority = new SimpleGrantedAuthority("ROLE_NEW_STUDENT");
                break;
            case "재학생":
                authority = new SimpleGrantedAuthority("ROLE_REGULAR_STUDENT");
                break;
            case "관리자":
                authority = new SimpleGrantedAuthority("ROLE_MANAGER");
                break;
            default:
                authority = new SimpleGrantedAuthority("ROLE_USER");
                break;
        }

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, null, List.of(authority));

        // 여기에 로깅 추가
        log.info("Assigned Authorities: {}", authenticationToken.getAuthorities());

        // 디테일 넣기
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
