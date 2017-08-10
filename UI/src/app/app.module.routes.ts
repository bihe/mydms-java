import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './components/home/home.component';

// import { AuthComponent } from "app/components/auth/auth.component";
// import { MasterDataComponent } from "app/components/masterdata/masterdata.component";

 const routes: Routes = [
    { path: '', component: HomeComponent, pathMatch: 'full' },
    { path: '**', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
  })
export class AppRoutingModule { }