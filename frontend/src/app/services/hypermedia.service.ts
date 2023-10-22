import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { EMPTY, Observable, throwError } from 'rxjs';
import { LinkResource } from '../models';

@Injectable({
  providedIn: 'root'
})
export class HypermediaService {
  constructor(private readonly http: HttpClient) {}

  fetch<T>(link: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(link, { params });
  }

  save<T>(link: string, body: Record<string, unknown>, params?: HttpParams): Observable<T> {
    return this.http.post<T>(link, body, { params } );
  }

  update<T>(link: string, body?: Record<string, unknown>, params?: HttpParams): Observable<T> {
    return this.http.put<T>(link, body, { params });
  }

  remove<T>(link: string, params?: HttpParams): Observable<T> {
    return this.http.delete<T>(link, { params });
  }

  get<T>(links: LinkResource, rel: string, resourceId?: string, params?: HttpParams): Observable<T> {
    let link = this.fetchLink(links, rel);

    if (link === '') {
      return throwError(() => new Error('Relation link has not found.'));
    }

    if (resourceId) {
      link = link + '/' + resourceId;
    }

    return this.http.get<T>(link, { params });
  }

  put<T>(links: LinkResource, rel: string, resourceId?: string, params?: HttpParams): Observable<T> {
    let link = this.fetchLink(links, rel);

    if (link === '') {
      return EMPTY;
    }

    if (resourceId) {
      link = link + '/' + resourceId;
    }

    return this.http.put<T>(link, params);
  }

  post<T>(links: LinkResource, rel: string, params?: HttpParams): Observable<T> {
    const link = this.fetchLink(links, rel);

    if (link === '') {
      return EMPTY;
    }

    return this.http.post<T>(link, params);
  }

  options<T>(links: LinkResource, rel: string): Observable<T> {
    const link = this.fetchLink(links, rel);

    if (link === '') {
      return EMPTY;
    }

    return this.http.options<T>(link);
  }

  delete<T>(links: LinkResource, rel: string): Observable<T> {
    const link = this.fetchLink(links, rel);

    if (link === '') {
      return EMPTY;
    }

    return this.http.delete<T>(link);
  }

  private fetchLink(links: LinkResource, rel: string): string {
    console.info(links, rel);
    let link = null;

    if (Array.isArray(links)) {
      links.forEach((linkObj) => {
        if (linkObj[rel] !== null || linkObj[rel] !== undefined) {
          link = linkObj[rel].href;
        }
      });
    } else {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      if (links[rel] !== undefined) {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        link = links[rel].href;
      }
    }

    if (link === null) {
      return '';
    }

    console.info(link);
    return this.cleanLink(link);
  }

  private cleanLink(link: string): string {
    if (link.indexOf('{')) {
      link = link.substring(0, link.indexOf('{'));
    }

    return link;
  }
}
