package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);  // 设置默认值为0，表示非默认地址
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        return addressBookMapper.SelectByUserId(BaseContext.getCurrentId());
    }

    @Override
    public AddressBook defaultAddress() {
        return addressBookMapper.SelectByUserId(BaseContext.getCurrentId()).get(0);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void delete(Integer id) {
        addressBookMapper.deleteById(id);
    }

    @Override
    public AddressBook getById(Integer id) {
        return addressBookMapper.selectById(id);
    }

    @Override
    public void setDefault(AddressBook addressBook) {
        // 先将当前用户的所有地址设置为非默认
        addressBookMapper.resetDefaultByUserId(BaseContext.getCurrentId());
        // 将指定地址设置为默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.updateDefault(addressBook.getId());
    }


}