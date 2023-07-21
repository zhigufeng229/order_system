package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.entity.AddressBook;
import com.order.chandler.mapper.AddressBookMapper;
import com.order.chandler.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
