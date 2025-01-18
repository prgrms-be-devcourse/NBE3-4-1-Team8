package com.example.backend.domain.member.dto;

import com.example.backend.domain.common.Address;

import lombok.Builder;

@Builder
public record MemberInfoResponse (String username, String nickname, Address address){
}
