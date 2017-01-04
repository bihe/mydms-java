export class Sender {
  public id:number;
  public name:string;
  public create:boolean = false;

  public toString() {
    if(this.create) {
      return this.name + ' [*]';
    }
    return this.name;
  }
}
