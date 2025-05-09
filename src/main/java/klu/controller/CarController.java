package klu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import klu.model.Car;
import klu.model.CarService;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/cars")
public class CarController {

	@Autowired
	CarService carService;

	@PostMapping("/admin/add")
	public String addCar(@RequestBody Car car)
	{
		return carService.addCar(car);
	}
	
	@GetMapping("/user/available")
	public String getAvailableCars()
	{
		return carService.getAvailableCars();
	}
	
	@GetMapping("/user/details/{id}")
	public String getCarById(@PathVariable("id") Long id)
	{
		return carService.getCarById(id);
	}
	
	@PutMapping("/admin/update")
	public String updateCar(@RequestBody Car car)
	{
		return carService.updateCar(car);
	}
	
	@DeleteMapping("/admin/delete/{id}")
	public String deleteCar(@PathVariable("id") Long id)
	{
		return carService.deleteCar(id);
	}
	
}
