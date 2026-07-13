package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    void insert(AddressBook addressBook);
    List<AddressBook> SelectByUserId(Long userId);
    void update(AddressBook addressBook);
    void deleteById(Integer id);
    AddressBook selectById(Integer id);
    void updateDefault(Long id);
    void resetDefaultByUserId(Long userId);
}