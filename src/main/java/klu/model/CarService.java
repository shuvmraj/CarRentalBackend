package klu.model;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.GsonBuilder;

import klu.repository.CarRepository;

@Service
public class CarService {

	@Autowired
	CarRepository carRepository;

	public String addCar(Car car) {
		try {
			carRepository.save(car);
			return "200::New car has been added";
		} catch (Exception e) {
			return "400::" + e.getMessage();
		}
	}

	public String getAllCars() {
		try {
			List<Car> carList = carRepository.findAll();
			return new GsonBuilder().create().toJson(carList);
		} catch (Exception e) {
			return "400::" + e.getMessage();
		}
	}

	public String getAvailableCars() {
		try {
			List<Car> carList = carRepository.findAll();
			List<Car> availableCars = carList.stream()
											 .filter(car -> car.isAvailabilityStatus())
											 .collect(Collectors.toList());
			return new GsonBuilder().create().toJson(availableCars);
		} catch (Exception e) {
			return "400::" + e.getMessage();
		}
	}

	public String getCarById(Long id) {
		try {
			Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
			return new GsonBuilder().create().toJson(car);
		} catch (Exception e) {
			return "404::Car not found or error retrieving data: " + e.getMessage();
		}
	}

	public String updateCar(Car car) {
		try {
			carRepository.findById(car.getId()).orElseThrow(() -> new RuntimeException("Car not found for update"));
			carRepository.save(car);
			return "200::Car details have been updated";
		} catch (Exception e) {
			return "400::Error updating car: " + e.getMessage();
		}
	}

	public String deleteCar(Long id) {
		try {
			carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found for deletion"));
			carRepository.deleteById(id);
			return "200::Car details have been deleted";
		} catch (Exception e) {
			return "404::Error deleting car: " + e.getMessage();
		}
	}

}
