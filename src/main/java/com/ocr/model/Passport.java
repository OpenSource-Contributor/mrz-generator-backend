package com.ocr.model;

import java.time.LocalDate;

public class Passport
{
	String passportNo;

	String country;

	String nationality;

	String gender;

	String givenName;

	String surname;

	LocalDate dob;

	LocalDate expiryDate;

	public String getPassportNo()
	{
		return passportNo;
	}

	public void setPassportNo( String passportNo )
	{
		this.passportNo = passportNo;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry( String country )
	{
		this.country = country;
	}

	public String getNationality()
	{
		return nationality;
	}

	public void setNationality( String nationality )
	{
		this.nationality = nationality;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender( String gender )
	{
		this.gender = gender;
	}

	public String getGivenName()
	{
		return givenName;
	}

	public void setGivenName( String givenName )
	{
		this.givenName = givenName;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname( String surname )
	{
		this.surname = surname;
	}

	public LocalDate getDob()
	{
		return dob;
	}

	public void setDob( LocalDate dob )
	{
		this.dob = dob;
	}

	public LocalDate getExpiryDate()
	{
		return expiryDate;
	}

	public void setExpiryDate( LocalDate expiryDate )
	{
		this.expiryDate = expiryDate;
	}
}
