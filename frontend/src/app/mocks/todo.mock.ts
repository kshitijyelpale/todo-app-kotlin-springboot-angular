import { Todo, TodoResources } from 'src/app/models';

export const TODO: Todo = {
  id: 1,
  name: 'todo name',
  description: 'some todo description',
  tasks: [
    { id: 1,
      name: 'task 1',
      description: 'task desc 1',
      completed: true
    },
    { id: 2,
      name: 'task 2',
      description: 'task desc 2',
      completed: false
    },
    { id: 3,
      name: 'task 3',
      description: 'task desc 3',
      completed: false
    }
  ],
  _links: {
    self: {
      href: 'https://'
    },
    first: {
      href: 'https://'
    },
    last: {
      href: 'https://'
    },
    next: {
      href: 'https://'
    },
    prev: {
      href: 'https://'
    }
  }
};

export const TODO_RESOURCES: TodoResources = {
  _embedded: {
    todoResources: [TODO]
  },
  _links: {
    self: {
      href: 'https://'
    },
    first: {
      href: 'https://'
    },
    last: {
      href: 'https://'
    },
    next: {
      href: 'https://'
    },
    prev: {
      href: 'https://'
    }
  }
};
