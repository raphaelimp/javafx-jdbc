package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dDao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){
		return dDao.findAll();
 	}
	
	public void saveOrUpdate(Department department) {
		if(department.getId() == null) {
			dDao.insert(department);
		} else {
			dDao.update(department);
		}
	}
	
	public void remove(Department department) {
		System.out.println(department);
		dDao.deleteById(department.getId());
		System.out.println(department);
	}
	
}
