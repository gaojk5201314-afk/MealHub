package com.sky.controller.user;


import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "用户地址相关接口")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @ApiOperation(value = "新增用户地址")
    @PostMapping
    public Result add(@RequestBody AddressBook addressBook) {
        log.info("新增用户地址: {}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    @ApiOperation(value = "查询用户地址列表")
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        return Result.success(addressBookService.list());
    }

    @ApiOperation(value = "查询用户默认地址")
    @GetMapping("/default")
    public Result<AddressBook> defaultAddress() {
        return Result.success(addressBookService.defaultAddress());
    }

    @ApiOperation(value = "修改用户地址")
    @PutMapping
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("修改用户地址: {}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    @ApiOperation(value = "删除用户地址")
    @DeleteMapping
    public Result delete(Integer id) {
        log.info("删除用户地址: {}", id);
        addressBookService.delete(id);
        return Result.success();
    }

    @ApiOperation("根据id查询用户地址")
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Integer id) {
        return Result.success(addressBookService.getById(id));
    }

    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

}
