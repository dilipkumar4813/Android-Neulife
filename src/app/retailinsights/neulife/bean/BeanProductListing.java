package app.retailinsights.neulife.bean;

public class BeanProductListing {

	private String productName;
	private String prod_code;
	private String prod_size;
	private String prod_o_price;
	private String prod_p_price;
	private String prod_availability;
	private String prod_quantity;
	private String img_url;
	private String flavours;
	private String rating;
	private String review;
	private String prod_id;
	private String ingrediants;
	private String discount;
	private String color;
	private String description;
	private String sku_id;
	private String sku_name;
	private String sku_code;
	private String facts;
	private String flavourCode;
	private String sizeCode;
	private String brandName;

	public BeanProductListing(/* String dates, String Products */) {
		// TODO Auto-generated constructor stub
		// super();
		/*
		 * this.productName = dates; this.prod_code = Products;
		 */
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return prod_code;
	}

	public void setProductCode(String prod_code) {
		this.prod_code = prod_code;
	}

	public String getProdSize() {
		return prod_size;
	}

	public void setProdSize(String prod_size) {
		this.prod_size = prod_size;
	}

	public String getOPrice() {
		return prod_o_price;
	}

	public void setOPrice(String prod_o_price) {
		this.prod_o_price = prod_o_price;
	}

	public String getPPrice() {
		return prod_p_price;
	}

	public void setPPrice(String prod_p_price) {
		this.prod_p_price = prod_p_price;
	}

	public String getPAvaiability() {
		return prod_availability;
	}

	public void setPAvaiability(String prod_availability) {
		this.prod_availability = prod_availability;
	}

	public String getProdQuantity() {
		return prod_quantity;
	}

	public void setProdQuantity(String prod_quantity) {
		this.prod_quantity = prod_quantity;
	}

	public String getImgUrl() {
		return img_url;
	}

	public void setImgUrl(String img_url) {
		this.img_url = img_url;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public void setProductId(String prod) {
		this.prod_id = prod;
	}

	public String getProductId() {
		return prod_id;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setFlavour(String flavours) {
		this.flavours = flavours;
	}

	public String getFlavour() {
		return flavours;
	}

	public void setIngrediants(String ingrediants) {
		this.ingrediants = ingrediants;
	}

	public String getIngrediants() {
		return ingrediants;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setSKUID(String sku_id) {
		this.sku_id = sku_id;
	}

	public String getSKUID() {
		return sku_id;
	}

	public void setSKUNAME(String sku_name) {
		this.sku_name = sku_name;
	}

	public String getSKUNAME() {
		return sku_name;
	}

	public void setSKUCODE(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getSKUCODE() {
		return sku_code;
	}

	public void setFacts(String facts) {
		this.facts = facts;
	}

	public String getFacts() {
		return facts;
	}

	public void setFlavourCode(String flavourCode) {
		this.flavourCode = flavourCode;
	}

	public String getFlavourCode() {
		return flavourCode;
	}

	public void setSizeCode(String sizeCode) {
		this.sizeCode = sizeCode;
	}

	public String getSizeCode() {
		return sizeCode;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandName() {
		return brandName;
	}
}
