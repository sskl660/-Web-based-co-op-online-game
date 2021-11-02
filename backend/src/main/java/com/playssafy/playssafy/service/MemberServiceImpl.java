package com.playssafy.playssafy.service;

import com.playssafy.playssafy.dto.member.Member;
import com.playssafy.playssafy.exception.MemberExistException;
import com.playssafy.playssafy.exception.MemberNotFoundException;
import com.playssafy.playssafy.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    public void addMember() {
        Member member = new Member("kimssafy","ssafy", true);
        memberRepository.save(member);
    }
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Member save(Member member) throws MemberExistException {

        Optional<Member> registeredUser = memberRepository.findMemberByUserId(member.getUserID());
        if (registeredUser.isPresent()) {
            throw new MemberExistException("[" + member.getUserID() + "] 등록된 사용자 입니다.");
        }

        return memberRepository.save(member);
    }

    @Override
    public Member update(Member member) throws MemberNotFoundException {
        Optional<Member> registeredUser = Optional.ofNullable(memberRepository.findMemberByUserId(member.getUserID())
                .orElseThrow(() -> new MemberNotFoundException("사용자 정보가 존재하지 않습니다.")));

        if (registeredUser.isPresent()) {
            member = memberRepository.save(member);
        }

        return member;
    }

    @Override
    public Member findUserByUserId(String userId) throws  MemberNotFoundException {
        Optional<Member> member = Optional.ofNullable(memberRepository.findMemberByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException("사용자 정보가 존재하지 않습니다.")));

        return member.get();
    }

    @Override
    public Member findById(Long id) throws MemberNotFoundException {
        Optional<Member> member = Optional.ofNullable(memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("사용자 정보가 존재하지 않습니다.")));

        return member.get();
    }

    @Override
    public void remove(Long id) {
        Optional<Member> member = Optional.ofNullable(memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("사용자 정보가 존재하지 않습니다.")));

        memberRepository.delete(member.get());
    }
}