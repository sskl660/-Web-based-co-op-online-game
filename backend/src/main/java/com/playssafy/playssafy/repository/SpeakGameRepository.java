package com.playssafy.playssafy.repository;

import com.playssafy.playssafy.dto.speaking.Speaking;
import org.springframework.data.repository.CrudRepository;

public interface SpeakGameRepository extends CrudRepository<Speaking, String> {
}