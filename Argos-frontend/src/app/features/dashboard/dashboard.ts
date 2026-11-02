import { Component } from '@angular/core';
import { NetworkMap } from '../network/network-map/network-map';

@Component({
  selector: 'app-dashboard',
  imports: [NetworkMap],
  templateUrl: './dashboard.html',
})
export class Dashboard {}
