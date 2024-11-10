import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class ProductDTO {
  @IsNotEmpty()
  @IsString()
  name: string;

  @IsNumber()
  price: number;

  @IsString()
  thumbnail?: string;

  @IsString()
  description: string;

  @IsNumber()
  category_id: number;

  constructor(name: string, price: number, thumbnail: string, description: string, category_id: number) {
    this.name = name;
    this.price = price;
    this.thumbnail = thumbnail;
    this.description = description;
    this.category_id = category_id;
  }
}
