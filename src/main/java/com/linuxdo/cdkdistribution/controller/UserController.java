package com.linuxdo.cdkdistribution.controller;

import com.linuxdo.cdkdistribution.dto.ApiResponse;
import com.linuxdo.cdkdistribution.dto.CdkDto;
import com.linuxdo.cdkdistribution.dto.ClaimRecordDto;
import com.linuxdo.cdkdistribution.dto.UserDto;
import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.service.CdkService;
import com.linuxdo.cdkdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CdkService cdkService;

    @Autowired
    public UserController(UserService userService, CdkService cdkService) {
        this.userService = userService;
        this.cdkService = cdkService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.ok(ApiResponse.error("Not authenticated"));
        }
        
        UserDto userDto = userService.convertToDto(currentUser);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @GetMapping("/me/cdks")
    public ResponseEntity<ApiResponse<List<CdkDto>>> getUserCdks() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.ok(ApiResponse.error("Not authenticated"));
        }
        
        List<Cdk> cdks = cdkService.getCdksClaimedByUser(currentUser);
        List<CdkDto> cdkDtos = cdks.stream()
                .map(cdkService::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(cdkDtos));
    }
}
