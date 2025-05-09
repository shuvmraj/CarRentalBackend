package klu.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cars")
public class Car {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(name="make")
  String make;
  @Column(name="model")
  String model;
  @Column(name="year")
  int year;
  @Column(name="rental_price_per_day")
  double rentalPricePerDay;
  @Column(name="availability_status")
  boolean availabilityStatus = true;
  
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getMake() {
	return make;
}
public void setMake(String make) {
	this.make = make;
}
public String getModel() {
	return model;
}
public void setModel(String model) {
	this.model = model;
}
public int getYear() {
	return year;
}
public void setYear(int year) {
	this.year = year;
}
public double getRentalPricePerDay() {
	return rentalPricePerDay;
}
public void setRentalPricePerDay(double rentalPricePerDay) {
	this.rentalPricePerDay = rentalPricePerDay;
}
public boolean isAvailabilityStatus() {
	return availabilityStatus;
}
public void setAvailabilityStatus(boolean availabilityStatus) {
	this.availabilityStatus = availabilityStatus;
}
@Override
public String toString() {
	return "Car [id=" + id + ", make=" + make + ", model=" + model + ", year=" + year + ", rentalPricePerDay="
			+ rentalPricePerDay + ", availabilityStatus=" + availabilityStatus + "]";
}

  
}