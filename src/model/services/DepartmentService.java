package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dDao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){
		return dDao.findAll();
 	}
	
}
