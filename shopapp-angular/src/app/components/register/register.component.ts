import { UserService } from '../../services/user.service';

import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { RegisterDTO } from '../../dots/user/register.dto';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  @ViewChild('registerForm') registerForm !: NgForm;
  phoneNumber : string;
  password : string;
  retypePassword : string;
  fullName : string;
  address : string;
  isAccepted: boolean;
  dateOfBirth : Date

  constructor(private router:Router,private userService: UserService){
    this.phoneNumber='';
    this.password='';
    this.retypePassword='';
    this.fullName='';
    this.address='';
    this.isAccepted=true;
    this.dateOfBirth=new Date();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear()-18);
  }

  onPhoneNumberChange(){
    console.log(`Phone Typed: ${this.phoneNumber}`);
  }

  

  register(){
    const message = 
    `phone: ${this.phoneNumber}`+ 
    `password :${this.password}`+
    `retypePassword :${this.retypePassword}`+
    `address :${this.address}`+
    `fullName :${this.fullName}`+
    `isAccepted :${this.isAccepted}`+
    `dateOfBirth :${this.dateOfBirth}`;


    alert(message)

    const registerDTO:RegisterDTO = {
      "fullname" :this.fullName,
      "phone_number" :this.phoneNumber,
      "address" : this.address,
      "password" : this.password,
      "retype_password" : this.retypePassword,
      "date_of_birth" :this.dateOfBirth,
      "facebook_account_id" : 0,
      "google_account_id" : 0,
      "role_id" :1
    }
    this.userService.register(registerDTO).subscribe({
      next: (response: any) => {
        debugger
        if (response && response.message === "Register successfully") {
          this.router.navigate(['/login']);
        } else {
          alert(`Unexpected response: ${response.message || 'Unknown error'}`);
        }
      },
      complete: () => {
        debugger
      },
      error: ( error: any) => {
        debugger
        alert(`Cannot register, error: ${error.error};
        }`)
      }

    });
  }

  checkPasswordMatch(){
    if(this.password !== this.retypePassword){
      this.registerForm.form.controls['retypePassword'].setErrors({'passwordMismatch':true});
    }
    else{
      this.registerForm.form.controls['retypePassword'].setErrors(null);
    }
  }

  checkAge(){
    if(this.dateOfBirth ){
      const today=new Date();
      const birthDay= new Date(this.dateOfBirth);
      let age=today.getFullYear() - birthDay.getFullYear();
      const monthDiff = today.getMonth()-birthDay.getMonth();
      if(monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDay.getDate())){
        age--;
      }
      if(age <18){
        this.registerForm.form.controls[`dateOfBirth`].setErrors({'invalidAge' : true});
      }
      else {
        this.registerForm.form.controls[`dateOfBirth`].setErrors(null);
      }
    }
    
  }

}