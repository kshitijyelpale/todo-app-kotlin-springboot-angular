import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ListComponent } from './components/todo/list/list.component';
import { EditComponent } from './components/todo/edit/edit.component';
import { ViewComponent } from './components/todo/view/view.component';

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
    path: 'todos/:todoId/edit',
    component: EditComponent
  },
  {
    path: 'todos/:todoId/view',
    component: ViewComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
