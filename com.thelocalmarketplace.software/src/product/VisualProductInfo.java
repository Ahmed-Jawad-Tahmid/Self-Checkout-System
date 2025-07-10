/*
Allan Kong, 30171801
Mark Cena, 30176936
Sarthak Singh, 30180318
Arjit Chitkara, 30169949
Kyle Ontiveros, 30193288
Ronaar Qureshi, 30147045
Sufian Tariq, 30179995
Ahmed Tahmid, 30175756
Labib Ahmed, 30194888
Ana DuCristea, 30170871
Kazi Badrul Arif, 30161466
Yazeed Badr, 30176535
Arshaum Hajinabi, 30189955
Juan Calvo Franco, 30192699 
Arpit Chitkara, 30170166
Saahim Salman, 30125256
Siddharth Sadhwani, 30194796
Farhan Labib, 30176224
Parushrut Dubey: 30196553
Tanjim Rahman, UCID: 30182328
Noelle Thundathil, 30115430 
 */

package product;


import com.thelocalmarketplace.hardware.Product;

public class VisualProductInfo {
	private Product product;
	private String imageURL;
	private String name;
	
	public VisualProductInfo(Product product, String imageURL, String name) {
		this.imageURL = imageURL;
		this.name = name;
		this.product =  product;
	}
	
	public String getName() {
		return name;
	}

	public String getImageURL() {
		return imageURL;
	}

	public Product getProduct() {
		return product;
	}

}
