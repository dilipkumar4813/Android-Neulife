package app.retailinsights.neulife.bean;

public class BeanAddressListing {

	private String addressId;
	private String addressLine1;
	private String addressLine2;
	private String phone;
	private String pincode;
	private String city;
	private String fullName;

	public BeanAddressListing(/* String dates, String Products */) {
		// TODO Auto-generated constructor stub
		// super();
	}

	public void setAddressId(String addId){
		this.addressId = addId;
	}
	
	public String getAddressId(){
		return addressId;
	}
	
	public void setAddressLine1(String add1){
		this.addressLine1 = add1;
	}
	
	public String getAddressLine1(){
		return addressLine1;
	}
	
	public void setAddressLine2(String add2){
		this.addressLine2 = add2;
	}
	
	public String getAddressLine2(){
		return addressLine2;
	}
	
	public void setPhone(String ph){
		this.phone = ph;
	}
	
	public String getPhone(){
		return phone;
	}
	
	public void setCity(String cty){
		this.city = cty;
	}
	
	public String getCity(){
		return city;
	}
	
	public void setPincode(String pin){
		this.pincode = pin;
	}
	
	public String getPincode(){
		return pincode;
	}
	
	public void setFullName(String name){
		this.fullName = name;
	}
	
	public String getFullName(){
		return fullName;
	}
}
