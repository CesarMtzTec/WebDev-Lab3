/*
 * ProductDAOJdbcImpl
 * Version 1.0
 * August 21, 2021 
 * Copyright 2021 Tecnologico de Monterrey
 */
package mx.tec.web.lab.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import mx.tec.web.lab.service.CommentsService;
import mx.tec.web.lab.vo.ProductVO;
import mx.tec.web.lab.vo.SkuVO;

/**
 * @author Enrique Sanchez
 *
 */
@Component("jdbc")
public class ProductDAOJdbcImpl implements ProductDAO {
	/** Id field **/
	public static final String ID = "id";
	
	/** Name field **/
	public static final String NAME = "name";
	
	/** Description field **/
	public static final String DESCRIPTION = "description";

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CommentsService commentService;
	
	/**
	 * Method for getting the child Skus of a Product.
	 * @param parentId The id of the Product to get the Skus from.
	 * @return
	 */
	public List<SkuVO> findChildSkus(final long parentId) {
		String sql = "SELECT * FROM Sku WHERE parentProduct_id = " + parentId;
		
		return jdbcTemplate.query(sql, (ResultSet rs) -> {
			List<SkuVO> skus = new ArrayList<>();
			while(rs.next()) {
				SkuVO sku = new SkuVO(
					rs.getLong(ID),
					rs.getString("color"),
					rs.getString("size"),
					rs.getDouble("listPrice"),
					rs.getDouble("salePrice"),
					rs.getLong("quantityOnHand"),
					rs.getString("smallImageUrl"),
					rs.getString("mediumImageUrl"),
					rs.getString("largeImageUrl")
				);
				skus.add(sku);	
			}
			return skus;
		});
	}
	
	@Override
	public List<ProductVO> findAll() {
		String sql = "SELECT id, name, description FROM Product";

		return jdbcTemplate.query(sql, (ResultSet rs) -> {
			List<ProductVO> list = new ArrayList<>();

			while(rs.next()){
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					findChildSkus(rs.getLong(ID)),
					commentService.getComments()
				);

				list.add(product);
			}
			
			return list;
		});
	}

	@Override
	public Optional<ProductVO> findById(long id) {
        String sql = "SELECT id, name, description FROM product WHERE id = ?";
        
		return jdbcTemplate.query(sql, new Object[]{id}, new int[]{java.sql.Types.INTEGER}, (ResultSet rs) -> {
			Optional<ProductVO> optionalProduct = Optional.empty();

			if(rs.next()){
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					findChildSkus(rs.getLong(ID)),
					commentService.getComments()
				);
				
				optionalProduct = Optional.of(product);
			}
			
			return optionalProduct;
		});
	}

	@Override
	public List<ProductVO> findByNameLike(String pattern) {
		String sql = "SELECT id, name, description FROM product WHERE name like ?";

		return jdbcTemplate.query(sql, new Object[]{"%" + pattern + "%"}, new int[]{java.sql.Types.VARCHAR}, (ResultSet rs) -> {
			List<ProductVO> list = new ArrayList<>();

			while(rs.next()){
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					new ArrayList<>(),
					commentService.getComments()
				);
				
				list.add(product);
			}
			
			return list;
		});
	}

	private void removeSkus(final long id) {
		String sql = "DELETE FROM Sku WHERE parentProduct_id = " + id;
		jdbcTemplate.query(sql, (ResultSet rs) -> {
			System.out.println(rs);
		});
	}
	
	@Override
	public ProductVO insert(ProductVO newProduct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ProductVO existingProduct) {
		long id = existingProduct.getId();
		String sql = "DELETE FROM Product WHERE id = " + id;
		jdbcTemplate.query(sql, (ResultSet rs) -> {
			System.out.println(rs);
		});
		removeSkus(id);
	}



	@Override
	public void update(ProductVO existingProduct) {
		// TODO Auto-generated method stub

	}

}
