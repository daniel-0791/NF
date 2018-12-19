package com.zoe.service;


import com.zoe.pojo.House;

import java.util.List;

public interface HouseService {

	List<House> list();
	List<House> listAndRequest();
	public List<House> findArea(String area);
	public  List<House> listHouse(String area,String rentNum,int rentPrice1,int rentPrice2,
								  String checkInDate,String sex,String way);
}

