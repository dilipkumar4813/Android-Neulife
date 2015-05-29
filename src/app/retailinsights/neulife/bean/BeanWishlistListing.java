package app.retailinsights.neulife.bean;

public class BeanWishlistListing {

	private String productName;
	private String wishlistId;
	private String price;
	private String image;
	private String sku;
	private String quantity;

	public BeanWishlistListing(/* String dates, String Products */) {
		// TODO Auto-generated constructor stub
		// super();
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	public void setwishlistId(String wishlistid) {
		this.wishlistId = wishlistid;
	}

	public String getwishlistId() {
		return wishlistId;
	}
	
	public void setPrice(String Price) {
		this.price = Price;
	}

	public String getPrice() {
		return price;
	}

	public void setImage(String img) {
		this.image = img;
	}

	public String getImage() {
		return image;
	}
	public void setSKU(String sku) {
		this.sku = sku;
	}

	public String getSKU() {
		return sku;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getQuantity() {
		return quantity;
	}
}
