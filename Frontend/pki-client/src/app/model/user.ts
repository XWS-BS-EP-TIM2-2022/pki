export class User {
    id!: number;
    email!: string;
    password!: string;
    name!: string;
    surname!: string;
    address!: string;
    role!: {
        name:string
    };
    commonName!: string;
    organizationName!: string;
    username!: string;
}