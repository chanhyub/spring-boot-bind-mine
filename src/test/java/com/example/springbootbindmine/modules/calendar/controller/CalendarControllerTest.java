package com.example.springbootbindmine.modules.calendar.controller;

import com.example.springbootbindmine.common.restdocs.RestDocsBasicTest;
import com.example.springbootbindmine.common.s3.service.S3Service;
import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;
import com.example.springbootbindmine.modules.calendar.request.CalendarSaveRequest;
import com.example.springbootbindmine.modules.calendar.request.CalendarUpdateRequest;
import com.example.springbootbindmine.modules.calendar.response.CalendarResponse;
import com.example.springbootbindmine.modules.calendar.service.CalendarService;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.example.springbootbindmine.modules.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CalendarController 테스트")
@WebMvcTest(CalendarController.class)
public class CalendarControllerTest extends RestDocsBasicTest {
    @MockitoBean
    CalendarService calendarService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    S3Service s3Service;

    @DisplayName("로그인 유저가 생성한 전체 캘린더 리스트를 조회한다.")
    @WithMockUser(username = "kakao_1", roles = "USER")
    @Test
    void getCalendarList() throws Exception {
        // given
        Optional<UserEntity> optionalUser = Optional.of(UserEntity.builder()
                .name("테스트 유저1")
                .email("test1@test.com")
                .imageFileLink("https://example.com/image1.jpg")
                .userName("kakao_1")
                .role(Role.USER)
                .build());
        Integer page = 0;
        Integer size = 10;

        List<CalendarResponse> calendarResponseList = new ArrayList<>();
        CalendarEntity calendarEntity = CalendarEntity.builder()
                .idx(1L)
                .title("테스트 캘린더")
                .description("테스트 캘린더 설명")
                .imageFileLink("https://example.com/image")
                .build();
        calendarResponseList.add(CalendarResponse.toDTO(calendarEntity));
        Page<CalendarResponse> calendarResponseDTOPage = new PageImpl<>(calendarResponseList, PageRequest.of(0, 10), 0);

        // when
        Mockito.when(userService.getUserByAuthentication()).thenReturn(optionalUser);
        Mockito.when(calendarService.getCalendarList(page, size, optionalUser.get())).thenReturn(calendarResponseDTOPage);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/calendar/list")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .with(csrf()))
                .andDo(print())
                .andDo(restDocs.document(
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                                ),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content").type(JsonFieldType.ARRAY)
                                                .description("캘린더 리스트"),
                                        fieldWithPath("content[].idx").type(JsonFieldType.NUMBER).description("캘린더 인덱스"),
                                        fieldWithPath("content[].title").type(JsonFieldType.STRING).description("캘린더 제목"),
                                        fieldWithPath("content[].description").type(JsonFieldType.STRING).description("캘린더 설명"),
                                        fieldWithPath("content[].imageFileLink").type(JsonFieldType.STRING).description("캘린더 이미지 링크"),

                                        fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("pageable.sort").type(JsonFieldType.OBJECT).description("페이지 정렬 정보"),
                                        fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 없음 여부"),
                                        fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안된 여부"),
                                        fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                        fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                        fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 안된 여부"),

                                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 사이즈"),
                                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("페이지 번호"),

                                        fieldWithPath("sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보 없음 여부"),
                                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안된 여부"),

                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 요소 수"),
                                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(calendarEntity.getTitle()))
                .andExpect(jsonPath("$.content[0].description").value(calendarEntity.getDescription()))
                .andExpect(jsonPath("$.content[0].imageFileLink").value(calendarEntity.getImageFileLink()));
    }

    @DisplayName("캘린더 리스트 조회 요청 시 로그인 유저의 정보가 존재하지 않을 경우 403 에러를 반환한다.")
    @WithMockUser(username = "kakao_1", roles = "USER")
    @Test
    void getCalendarListInActiveUser() throws Exception {
        // given
        Optional<UserEntity> optionalUser = Optional.empty();
        Integer page = 0;
        Integer size = 10;

        // when
        Mockito.when(userService.getUserByAuthentication()).thenReturn(optionalUser);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/calendar/list")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .with(csrf()))
                .andDo(print())
                .andDo(restDocs.document(
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP Status Code"),
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("에러 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                                )
                        )
                )
                .andExpect(status().isForbidden());
    }

    @DisplayName("새로운 캘린더를 생성한다.")
    @WithMockUser(username = "kakao_1", roles = "USER")
    @Test
    void createCalendar() throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test".getBytes()
        );

        CalendarSaveRequest calendarSaveRequest = new CalendarSaveRequest("테스트 캘린더", "테스트 캘린더 설명");
        String content = objectMapper.writeValueAsString(calendarSaveRequest);
        MockMultipartFile requestJson = new MockMultipartFile("calendarSaveRequest", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        Optional<UserEntity> optionalUser = Optional.of(UserEntity.builder()
                .name("테스트 유저1")
                .email("test1@test.com")
                .imageFileLink("https://example.com/image1.jpg")
                .userName("kakao_1")
                .role(Role.USER)
                .build());

        String imageFileUrl = "https://example.com/image";

        CalendarResponse calendarResponse = new CalendarResponse(1L, "테스트 캘린더", "테스트 캘린더 설명", "https://example.com/image");

        // when
        Mockito.when(userService.getUserByAuthentication()).thenReturn(optionalUser);
        Mockito.when(s3Service.upload(imageFile)).thenReturn(imageFileUrl);
        Mockito.when(calendarService.saveCalendar(any(), any(), any())).thenReturn(calendarResponse);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.multipart("/calendar/save")
                        .file(imageFile)
                        .file(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                        .contentType(MediaType.MULTIPART_MIXED)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andDo(restDocs.document(
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                                ),
                                requestParts(
                                        partWithName("imageFile").description("캘린더 이미지 파일").optional(),
                                        partWithName("calendarSaveRequest").description("캘린더 저장 요청")
                                ),
                                responseFields(
                                        fieldWithPath("idx").type(JsonFieldType.NUMBER).description("캘린더 인덱스"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("캘린더 제목"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("캘린더 설명"),
                                        fieldWithPath("imageFileLink").type(JsonFieldType.STRING).description("캘린더 이미지 링크")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(calendarResponse.title()))
                .andExpect(jsonPath("$.description").value(calendarResponse.description()))
                .andExpect(jsonPath("$.imageFileLink").value(calendarResponse.imageFileLink()));
    }

    @DisplayName("캘린더를 수정한다.")
    @Test
    void updateCalendar() throws Exception {
        // given
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test".getBytes()
        );

        CalendarUpdateRequest calendarUpdateRequest = new CalendarUpdateRequest(1L, "테스트 캘린더", "테스트 캘린더 설명", false);
        String content = objectMapper.writeValueAsString(calendarUpdateRequest);
        MockMultipartFile requestJson = new MockMultipartFile("calendarUpdateRequest", "jsondata", "application/json", content.getBytes(StandardCharsets.UTF_8));

        String imageFileUrl = "https://example.com/image";

        CalendarResponse calendarResponse = new CalendarResponse(1L, "테스트 캘린더", "테스트 캘린더 설명", imageFileUrl);

        // when
        Mockito.when(s3Service.upload(imageFile)).thenReturn(imageFileUrl);
        Mockito.when(calendarService.updateCalendar(any(), any())).thenReturn(calendarResponse);

        // then
        MockMultipartHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.multipart("/calendar/update");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(builder
                        .file(imageFile)
                        .file(requestJson)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                        .contentType(MediaType.MULTIPART_MIXED)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andDo(restDocs.document(
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                                ),
                                requestParts(
                                        partWithName("imageFile").description("캘린더 이미지 파일").optional(),
                                        partWithName("calendarUpdateRequest").description("캘린더 저장 요청")
                                ),
                                responseFields(
                                        fieldWithPath("idx").type(JsonFieldType.NUMBER).description("캘린더 인덱스"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("캘린더 제목"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("캘린더 설명"),
                                        fieldWithPath("imageFileLink").type(JsonFieldType.STRING).description("캘린더 이미지 링크").optional()
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(calendarResponse.title()))
                .andExpect(jsonPath("$.description").value(calendarResponse.description()))
                .andExpect(jsonPath("$.imageFileLink").value(calendarResponse.imageFileLink()));
    }

    @DisplayName("캘린더를 삭제한다.")
    @Test
    void deleteCalendar() throws Exception {
        // given
        Long idx = 1L;

        // when
        Mockito.doNothing().when(calendarService).deleteCalendar(idx);

        // then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/calendar/delete/{idx}", idx)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andDo(restDocs.document(
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("idx").description("캘린더 인덱스")
                                )
                        )
                )
                .andExpect(status().isOk());
    }


}
