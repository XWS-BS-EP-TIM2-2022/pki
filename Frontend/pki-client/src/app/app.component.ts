import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'pki-client';
  constructor(public router: Router) { }

  checkUrl() {
    if (window.location.href.indexOf("/login") > -1 || window.location.href.indexOf("/register") > -1)
      return false;

    return true;
  }
}
