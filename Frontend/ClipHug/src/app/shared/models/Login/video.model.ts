export class VideoModel {
  constructor(
    public videoId: string,
    public uploadDate: string,
    public fileName: string,
    public size: number,
    public visibility: string,
    public authorId: string
  ) {
  }
}
