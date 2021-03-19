package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entitiies.Department;

public class DepartmentService {
	
	public List<Department> findAll(){
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "Receptiom"));
		list.add(new Department(2, "Financial"));
		list.add(new Department(3, "IT"));
		return list;
 	}
	
}
