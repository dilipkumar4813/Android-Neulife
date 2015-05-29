package app.retailinsights.neulife.bean;

public class BeanOrderListing {

	private String orderId;
	private String orderDate;
	private String orderTitle;
	private String expectedDeliveryDate;
	private String status;
	private String image;

	public BeanOrderListing() {
		
	}

	public void setOrderId(String odId){
		this.orderId = odId;
	}
	
	public String getOrderId(){
		return orderId;
	}
	
	public void setOrderDate(String oDate){
		this.orderDate = oDate;
	}
	
	public String getOrderDate(){
		return orderDate;
	}
	
	public void setOrderTitle(String oTitle){
		this.orderTitle = oTitle;
	}
	
	public String getOrderTitle(){
		return orderTitle;
	}
	
	public void setDeliveryDate(String dDate){
		this.expectedDeliveryDate = dDate;
	}
	
	public String getDeliveryDate(){
		return expectedDeliveryDate;
	}
	
	public void setStatus(String stats){
		this.status = stats;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setImage(String img){
		this.image = img;
	}
	
	public String getImage(){
		return image;
	}
}
