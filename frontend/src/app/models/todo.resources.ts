import { Todo } from './todo.model';
import { LinkResource } from './link.resource';

export interface TodoResources {
  _embedded: {
    todoResources: Todo[]
  };
  _links: LinkResource;
}
