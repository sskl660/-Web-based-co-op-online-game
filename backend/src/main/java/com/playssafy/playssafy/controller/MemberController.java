package com.playssafy.playssafy.controller;

import com.playssafy.playssafy.dto.member.Member;
import com.playssafy.playssafy.exception.MemberExistException;
import com.playssafy.playssafy.exception.MemberNotFoundException;
import com.playssafy.playssafy.service.MemberService;
import com.playssafy.playssafy.service.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@Api(tags={"3.Member"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberServiceImpl memberService;

    /**
     * 사용자 등록
     *
     * @param member 등록 사용자 정보
     * @return ResponseEntity<Member> 등록 사용자 정보
     * @throws MemberExistException 사용자 중복 등록시 예외 처리
     */
    @ApiOperation(value = "맴버 입력")
    @PostMapping("/register")
    @ResponseBody
    @CachePut(value = "member", key = "#userId")
    public ResponseEntity<Member> registerUser(@RequestBody Member member) throws MemberExistException {
        return new ResponseEntity<>(memberService.save(member), HttpStatus.OK);
    }

    /**
     * 사용자 수정
     *
     * @param member 수정 사용자 정보
     * @return ResponseEntity<Member> 수정 사용자 정보
     * @throws MemberNotFoundException 사용자 정보가 존재하지 않은 경우 예외 처리
     */
    @PostMapping("/update")
    public ResponseEntity<Member> updateUser(@RequestBody Member member) throws MemberNotFoundException {
        return new ResponseEntity<>(memberService.update(member), HttpStatus.OK);
    }

    /**
     * 사용자 조회
     *
     * @param userId 그룹ID
     * @return ResponseEntity<Member> 사용자 정보
     * @throws MemberNotFoundException 사용자 정보가 존재하지 않은 경우 예외 처리
     */
    @ApiOperation(value = "맴버 조회")
    @PostMapping("/{userId}")
    @ResponseBody
    @Cacheable(value = "member" , key = "#userId")
    public ResponseEntity<Member> findUser(@PathVariable String userId) throws MemberNotFoundException {
        return new ResponseEntity<>(memberService.findUserByUserId(userId), HttpStatus.OK);
    }

    /**
     * 사용자 삭제
     *
     * @param id User 객체 Key
     * @return ResponseEntity<String> 삭제 처리 메세지
     * @throws MemberNotFoundException 사용자 정보가 존재하지 않은 경우 예외 처리
     */
    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable String id) throws MemberNotFoundException {
        memberService.remove(Long.parseLong(id));

        return new ResponseEntity<>("사용자 정보가 정상적으로 삭제 되었습니다.", HttpStatus.OK);
    }
}

