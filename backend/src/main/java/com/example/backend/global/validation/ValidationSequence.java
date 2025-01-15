package com.example.backend.global.validation;

import static com.example.backend.global.validation.ValidationGroups.*;

import jakarta.validation.GroupSequence;

/**
 * ValidationSequence
 * 검증 어노테이션의 순서를 지정하는 인터페이스
 * <p>NotNullGroup(@NotNull) → NotBlankGroup(@NotBlank) → ValidEnumGroup(@ValidEnum) → <br>
 * → PatternGroup(@Pattern) → SizeGroup(@Size)
 * → MinGroup(@Min) → MaxGroup(@Max)</p>
 *
 * @author : Kim Dong O
 */

@GroupSequence({NotNullGroup.class, NotBlankGroup.class, SizeGroup.class, MinGroup.class, MaxGroup.class,
	ValidEnumGroup.class,
	PatternGroup.class})
public interface ValidationSequence {
}
