package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao sDao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){
		return sDao.findAll();
 	}
	
	public void saveOrUpdate(Seller Seller) {
		if(Seller.getId() == null) {
			sDao.insert(Seller);
		} else {
			sDao.update(Seller);
		}
	}
	
	public void remove(Seller Seller) {
		sDao.deleteById(Seller.getId());
	}
	
}
