import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListComponent } from './components/todo/list/list.component';
import { EditComponent } from './components/todo/edit/edit.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'todos',
    pathMatch: 'full'
  },
  {
    path: 'todos',
    component: ListComponent
  },
  {
    path: 'todos/create',
    component: EditComponent
  },
  {
    path: 'todos/:todoId',
    component: EditComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
