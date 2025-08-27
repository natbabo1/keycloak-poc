// auth/jwt.guard.ts
import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { verifyAccessToken } from './verify-token';

@Injectable()
export class JwtGuard implements CanActivate {
  async canActivate(ctx: ExecutionContext) {
    const req = ctx.switchToHttp().getRequest();
    const auth = req.headers.authorization || '';
    if (!auth) throw new UnauthorizedException('No Authorization header');
    try {
      req.user = await verifyAccessToken(auth);
      return true;
    } catch {
      throw new UnauthorizedException('Invalid token');
    }
  }
}
