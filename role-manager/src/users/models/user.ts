import { Column, Entity, PrimaryGeneratedColumn } from "typeorm";
import { Role } from "../utils/roleEnum";



@Entity()
export class User {

    @PrimaryGeneratedColumn()
    id: number;

    @Column()
    email: string;

    @Column()
    password: string;

    @Column({
        type: "enum",
        enum: Role
    })
    role: Role;
}