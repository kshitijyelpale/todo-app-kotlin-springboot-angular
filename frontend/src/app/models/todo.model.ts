import { Task } from './task.model';
import { LinkResource } from './link.resource';

export interface Todo {
  id: number;
  name: string;
  description: string;
  tasks: Task[];
  _links: LinkResource;
}
