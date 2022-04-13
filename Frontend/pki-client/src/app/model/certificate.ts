import { User } from "./user";

export class Certificate {
    serialNumber!: string;
    issuerEmail!: string;
    issuerCertificateSerialNum!: string;
    subjectEmail!: string;
    level!: number;
    user!: User;
    certificateName!: string;
}

export interface CertificateDto {
    issuerSerialNumber: string;
    validFrom: Date;
    validTo: Date;
    subjectId: number;
    issuerId: number;
    isCA: boolean;
}

export class CertificateViewModel {
    serialNumber!: string;
    subject!: string;
    issuer!: string;
    validFrom!: Date;
    validTo!: Date;
    isRevoked!: boolean;
}