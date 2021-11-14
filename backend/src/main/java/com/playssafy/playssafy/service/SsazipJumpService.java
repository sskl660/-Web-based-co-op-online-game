package com.playssafy.playssafy.service;

import com.playssafy.playssafy.repository.SsazipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SsazipJumpService {
    @Autowired
    private SsazipRepository ssazipRepository;
}
