package app.retailinsights.neulife.bean;

public class BeanProductDetails {

	private String productName;
	private String prod_code;
	private String prod_size;
	private String prod_p_price;
	private String prod_o_price;
	private String img_url;
	private String flavours;
	private String rating;

	public BeanProductDetails(/*String dates, String Products*/) {
		// TODO Auto-generated constructor stub
		// super();
		/*this.productName = dates;
		this.prod_code = Products;*/
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductQuantity() {
		return prod_code;
	}

	public void setProductQuantity(String productQuantity) {
		this.prod_code = productQuantity;
	}
	public String getProdSize() {
		return prod_size;
	}

	public void setProdSize(String prod_size) {
		this.prod_size = prod_size;
	}
	public String getProdOPrice() {
		return prod_o_price;
	}

	public void setProdOPrice(String prod_o_price) {
		this.prod_o_price = prod_o_price;
	}
	public String getProdPPrice() {
		return prod_o_price;
	}

	public void setProdPPrice(String prod_price) {
		this.prod_p_price = prod_price;
	}
	
	public String getImgUrl() {
		return img_url;
	}

	public void setImgUrl(String img_url) {
		this.img_url = img_url;
	}
	public String getPossessionDate() {
		return flavours;
	}

	public void setPossessionDate(String flavours) {
		this.flavours = flavours;
	}
	public String getrating() {
		return rating;
	}

	public void setrating(String rating) {
		this.rating = rating;
	}

}
