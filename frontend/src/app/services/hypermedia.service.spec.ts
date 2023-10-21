import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { HypermediaService } from './hypermedia.service';

describe('HypermediaService', () => {
  let service: HypermediaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(HypermediaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
