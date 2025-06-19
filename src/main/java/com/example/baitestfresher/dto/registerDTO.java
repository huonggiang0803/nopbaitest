package com.example.baitestfresher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class registerDTO {
    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Họ tên không được để trống")
    private String userName;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 5, message = "Mật khẩu phải có ít nhất 5 ký tự")
    private String passWord;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải đủ 10 số và bắt đầu từ số 0")
    private String phoneNumber;

    private Byte is_deleted;
    private String avatar;
    private String type;


}
