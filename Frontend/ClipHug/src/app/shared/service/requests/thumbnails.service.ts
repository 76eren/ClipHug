import { Injectable } from "@angular/core";
import { DomSanitizer, SafeUrl } from "@angular/platform-browser";

@Injectable({
  providedIn: 'root',
})
export class ThumbnailsService {
  private thumbnails: SafeUrl[] = [];

  constructor(private sanitizer: DomSanitizer) {}

  public getThumbnailByNumber(num: number): SafeUrl | null {
    return num >= 0 && num < this.thumbnails.length ? this.thumbnails[num] : null;
  }

  public setThumbnails(thumbnails: string[]): void {
    this.thumbnails = thumbnails.map(thumbnail => {
      const objectURL = 'data:image/jpeg;base64,' + thumbnail;
      return this.sanitizer.bypassSecurityTrustUrl(objectURL);
    });
  }
}
