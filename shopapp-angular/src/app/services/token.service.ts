import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private readonly TOKEN_KEY=`access_token`;
  private jwtHelper = new JwtHelperService();
  constructor(){}
  getToken():string {
    return localStorage.getItem(this.TOKEN_KEY)??'';
}
setToken(token: string): void {        
    localStorage.setItem(this.TOKEN_KEY, token);             
}
getUserId(): number {
    // debugger
    let userObject = this.jwtHelper.decodeToken(this.getToken() ?? '');
    return 'userId' in userObject ? parseInt(userObject['userId']) : 0;
}
  
removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
}              
isTokenExpired(): boolean { 
    if(this.getToken() == null) {
        return false;
    }       
    return this.jwtHelper.isTokenExpired(this.getToken()!);
}

}
