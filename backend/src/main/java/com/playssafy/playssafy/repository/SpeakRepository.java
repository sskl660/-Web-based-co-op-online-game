package com.playssafy.playssafy.repository;

import com.playssafy.playssafy.dto.speaking.Speaking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakRepository extends JpaRepository<Speaking, String> {
}