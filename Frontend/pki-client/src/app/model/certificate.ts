import { User } from "./user";

export class Certificate {
    serialNumber!: string;
    issuerEmail!: string;
    issuerCertificateSerialNum!: string;
    subjectEmail!: string;
    level!: number;
    user!: User;
}