import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Pipe({
  name: 'safeUrl'
})
export class SafeUrlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(file: File): SafeUrl {
    // Tạo URL tạm thời từ file và đảm bảo an toàn
    return this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(file));
  }
}
