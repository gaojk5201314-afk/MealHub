package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void add(AddressBook addressBook);
    List<AddressBook> list();
    AddressBook defaultAddress();
    void update(AddressBook addressBook);
    void delete(Integer id);
    AddressBook getById(Integer id);
    void setDefault(AddressBook addressBook);
}
