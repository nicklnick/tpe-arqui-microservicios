import { Column, Entity, PrimaryGeneratedColumn, Unique } from "typeorm";
import { Role } from "../utils/roleEnum";



@Entity()
export class User {

    @PrimaryGeneratedColumn()
    id: number;


    @Column()
    name: string;

    
    @Column({unique: true})
    email: string;

    @Column()
    password: string;

    @Column({
        type: "enum",
        enum: Role
    })
    role: Role;
}