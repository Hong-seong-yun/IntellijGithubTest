package boardExample.simpleBoard.controller.api;

import boardExample.simpleBoard.domain.Comment;
import boardExample.simpleBoard.domain.Member;
import boardExample.simpleBoard.dto.CommentDto;
import boardExample.simpleBoard.dto.MemberDto;
import boardExample.simpleBoard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;
    private final HttpSession httpSession;
    // RequestBody : JSON데이터를 원하는 타입의 객체로 변환해야하는 경우 주로 사용.
    //create
    @PostMapping("/boards/{id}/comments")
    public ResponseEntity commentSave(@PathVariable("id") Long id, @RequestBody CommentDto commentDto, Authentication authentication) {
        MemberDto.UserSessionDto user = (MemberDto.UserSessionDto) httpSession.getAttribute("user");
        String uid = null;
//        세션일때와 아닐때로 구글로그인연동인지 일반 로그인인지를 구분
        if (user != null) {
            uid = user.getUid();
        }
        else {
            Member member = (Member) authentication.getPrincipal();  //userDetail 객체를 가져옴
            uid = member.getUid();
        }
        return ResponseEntity.ok(commentService.commentSave(uid, id, commentDto));
    }
    //update
    @PutMapping({"/boards/{bid}/comments/{cid}"})
    public ResponseEntity update(@PathVariable Long bid, @PathVariable Long cid, @RequestBody CommentDto commentDto) {
        commentService.update(cid, commentDto);
        return ResponseEntity.ok(cid);
    }

    //delete
    @DeleteMapping("/boards/{bid}/comments/{cid}")
    public ResponseEntity delete(@PathVariable Long bid, @PathVariable Long cid) {
        commentService.delete(cid);
        return ResponseEntity.ok(cid);
    }
    @PostMapping(value = "/boards/{boardId}/comments/reply")
    public ResponseEntity replySave(@PathVariable("boardId") Long boardId, @RequestBody CommentDto.Response response, Authentication authentication, @PageableDefault Pageable pageable) {
        MemberDto.UserSessionDto user = (MemberDto.UserSessionDto) httpSession.getAttribute("user");
        String uid = null;
//        세션일때와 아닐때로 구글로그인연동인지 일반 로그인인지를 구분
        if (user != null) {
            uid = user.getUid();
        }
        else {
            Member member = (Member) authentication.getPrincipal();  //userDetail 객체를 가져옴
            uid = member.getUid();
        }
        return ResponseEntity.ok(commentService.parentSave(uid, boardId, response.getParentId(), response));
    }
}